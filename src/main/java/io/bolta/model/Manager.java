package io.bolta.model;

import java.util.Objects;

/**
 * Represents a manager contact information.
 * 담당자 연락처 정보를 나타냅니다.
 */
public final class Manager {
    /**
     * Email address of the manager
     * 담당자 이메일 주소
     */
    private final String email;

    /**
     * Name of the manager
     * 담당자 이름
     */
    private final String name;

    /**
     * Telephone number of the manager
     * 담당자 전화번호
     */
    private final String telephone;

    private Manager() {
        this.email = null;
        this.name = null;
        this.telephone = null;
    }

    private Manager(String email, String name, String telephone) {
        if (email == null)
            throw new NullPointerException("email is marked non-null but is null");
        this.email = email;
        this.name = name;
        this.telephone = telephone;
    }

    private Manager(Builder builder) {
        this.email = builder.email;
        this.name = builder.name;
        this.telephone = builder.telephone;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getTelephone() {
        return telephone;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String email;
        private String name;
        private String telephone;

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder telephone(String telephone) {
            this.telephone = telephone;
            return this;
        }

        public Manager build() {
            if (email == null)
                throw new NullPointerException("email is marked non-null but is null");
            return new Manager(this);
        }
    }

    @Override
    public String toString() {
        return "Manager{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", telephone='" + telephone + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Manager manager = (Manager) o;
        return Objects.equals(email, manager.email) &&
                Objects.equals(name, manager.name) &&
                Objects.equals(telephone, manager.telephone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, name, telephone);
    }
}
