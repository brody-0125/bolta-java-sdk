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

    /**
     * Returns the email address of the manager.
     * <p>
     * 담당자의 이메일 주소를 반환합니다.
     *
     * @return the manager's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the name of the manager.
     * <p>
     * 담당자의 이름을 반환합니다.
     *
     * @return the manager's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the telephone number of the manager.
     * <p>
     * 담당자의 전화번호를 반환합니다.
     *
     * @return the manager's telephone number
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * Creates a new builder for Manager.
     * <p>
     * Manager를 위한 새 빌더를 생성합니다.
     *
     * @return a new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for constructing Manager instances.
     * <p>
     * Manager 인스턴스를 생성하기 위한 빌더입니다.
     */
    public static class Builder {
        private String email;
        private String name;
        private String telephone;

        /**
         * Sets the email address of the manager.
         * <p>
         * 담당자의 이메일 주소를 설정합니다.
         *
         * @param email the manager's email address (required)
         * @return this builder
         */
        public Builder email(String email) {
            this.email = email;
            return this;
        }

        /**
         * Sets the name of the manager.
         * <p>
         * 담당자의 이름을 설정합니다.
         *
         * @param name the manager's name
         * @return this builder
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the telephone number of the manager.
         * <p>
         * 담당자의 전화번호를 설정합니다.
         *
         * @param telephone the manager's telephone number
         * @return this builder
         */
        public Builder telephone(String telephone) {
            this.telephone = telephone;
            return this;
        }

        /**
         * Builds a new Manager instance.
         * <p>
         * 새 Manager 인스턴스를 생성합니다.
         *
         * @return the configured Manager
         * @throws NullPointerException if email is null
         */
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
